import re
input='Chicago->Urbana,Urbana->Springfield,Chicago->Lafayette,Los Angeles->Las Vegas,Las Vegas->New Jersey'
result=re.split(',|->',input)
print(result)