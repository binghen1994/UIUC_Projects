#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string>
#include <iostream>
#include <fstream>
#include <sstream>

#define MAXDATASIZE 100// max number of bytes we can get at once 

//get sockaddr, IPV4 or IPV6
void *get_addr(struct sockaddr *sa)
    {
        if (sa->sa_family == AF_INET) {
            return &(((struct sockaddr_in*)sa)->sin_addr);
        }

        return &(((struct sockaddr_in6*)sa)->sin6_addr);
    }

void parseaddr(std::string argvstring, std::string& hostname, std::string& path)
{
    int i = 0;
    char buf;
    if ((i = argvstring.find("://")) != std::string::npos)
    {
        i = i + 3;
    }
    for (; argvstring[i] != '/' && i < size(argvstring)-1; i++)
    {
        buf = argvstring[i];
        hostname.push_back(buf);
    }
    for (; i < size(argvstring); i++)
    {
        buf = argvstring[i];
        path.push_back(argvstring[i]);
    }

}

int main(int argc, char* argv[])
{   
    //std::cout<<"running"<<std::endl;
    int sockfd, numbytes;  
    char buf[MAXDATASIZE];
    struct addrinfo hints, *servinfo, *p;
    int rv;
    std::ofstream outfile("output");
    char s[INET6_ADDRSTRLEN];
    std::string reqmsg;
    std::string hostname;
    std::string path;

    // return if there's more than 1 argument.
    if(argc!=2) {return(1);}

    // check if protocol is http
    std::string argvstring;
    argvstring.append(argv[1]);
    if(argvstring.find("http")==std::string::npos)
    {   
        //std::cout<<"INVALIDPROTOCOL"<<std::endl;
        outfile<<"INVALIDPROTOCOL";
        return(1);
    }

    parseaddr(argvstring, hostname, path);

    memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
    
    char addrinput[size(argvstring)+1];
    strcpy(addrinput,hostname.c_str());

    //std::cout<<"hostname= "<<hostname<<"path="<<path<<std::endl;


	if ((rv = getaddrinfo(addrinput, "http", &hints, &servinfo)) != 0) 
    {
		//std::cout<<"error="<<gai_strerror(rv)<<std::endl;
		return 1;
	}

    for(p = servinfo; p != NULL; p = p->ai_next) 
    {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("client: socket");
			continue;
		}

		if (connect(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			close(sockfd);
			perror("client: connect");
			continue;
		}

		break;
	}
    //std::cout<<"connection"<<std::endl;
	if (p == NULL) {
        outfile<<"NOCONNECTION";
		return(1);
	}

   // inet_ntop(p->ai_family, get_addr((struct sockaddr *)p->ai_addr),
   //			s, sizeof s);

    
    // send request to server
    reqmsg.append("GET ");
    reqmsg.append(path);
    reqmsg.append(" HTTP/1.1\r\n");
    reqmsg.append("Host: ");
    reqmsg.append(hostname);
    reqmsg.append("\r\n\r\n");

    //std::cout<<reqmsg;
    char reqmsgchar[size(reqmsg)+1];
    strcpy(reqmsgchar,reqmsg.c_str());
    
    if(int i= send(sockfd,reqmsgchar,reqmsg.size(),0)==-1)
    {   
        perror("send failed");
    }

    //save received data in outstring
    std::string outstring;
    int dataLength;
    while((dataLength=recv(sockfd,buf,sizeof(buf),0))>0)
    {
        outstring.append(buf);
    }

    if(dataLength==-1)
    {
        perror("recverror");
        return(1);
    }
    //std::cout<<"data received is="<<outstring<<std::endl;
    if(outstring.find("404")!=std::string::npos)
    {
        outfile<<"FILENOTFOUND";
        perror("404 NOT FOUND");
        return(1);
    }

    //PARSE HEADER
    //check if received all the msg
    int receivelength=outstring.size();
    std::string msgbody;
    for(int j=outstring.find("\r\n\r\n")+4;j<receivelength;j++)
    {
        msgbody.push_back(outstring[j]);
    }
    int lengthindex=outstring.find("Content-Length");
    lengthindex += 15;

    std::string sInt = "";
    for(int i=lengthindex;outstring[i]!='\r';i++)
    {
        if(isdigit(outstring[i])){
            sInt += outstring[i];
        }
    }
    int filelength = std::stoi(sInt);

    if(filelength!=msgbody.size())
    {
        perror("length not correct");
        outfile<<msgbody;
        std::cout<<"filelength="<<filelength<<std::endl<<"receivelength="<<msgbody.size()<<std::endl;
        return(1);
    }

    //outfile msg body
    outfile<<msgbody;


    freeaddrinfo(servinfo); // all done with this structure
    close(sockfd);
    outfile.close();


    return(0);

}