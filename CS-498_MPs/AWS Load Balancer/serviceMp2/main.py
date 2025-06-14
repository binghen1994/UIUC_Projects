from flask import Flask, redirect, url_for, request
app = Flask(__name__)

seed=0

@app.route('/', methods=['GET'])
def getSeed():
   return str(seed)


@app.route('/', methods=['POST'])
def setSeed():
   print(request.get_json())
   print(type(request.get_json()))
   seedDict=request.get_json()
   global seed
   seed=seedDict['num']
   return str(seedDict['num'])

if __name__ == '__main__':
   app.run(host="0.0.0.0")