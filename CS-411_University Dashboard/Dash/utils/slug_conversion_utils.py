

def toNameSlug(name:str):
    return name.replace(' ', '+')


def toName(slug:str):
    return slug.replace('+', ' ')