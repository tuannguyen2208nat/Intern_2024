from setuptools import setup

APP = ['physical.py']  # Replace with the name of your script file
DATA_FILES = []
OPTIONS = {
    'argv_emulation': True,
    'packages': ['serial', 'tkinter'],
}

setup(
    app=APP,
    data_files=DATA_FILES,
    options={'py2app': OPTIONS},
    setup_requires=['py2app', 'wheel'],
)
