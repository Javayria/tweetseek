import os


def allowed_file(filename):
    ALLOWED_EXTENSIONS = {"png","jpg","jpeg"}
    _, ext = os.path.splitext(filename)
    return ext.lstrip('.').lower() in ALLOWED_EXTENSIONS