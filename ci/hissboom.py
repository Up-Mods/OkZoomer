# Copyright © 2024 Ennui Langeweile, All rights reserved.
#
# ..It ain't rocket science though, expect this to be open-sourced somewhere else

from typing import IO
import argparse
import glob
import tempfile
import zipfile

def hissboomify_zip(origin: IO[bytes], target: IO[bytes], is_jij: bool):
    zip = zipfile.ZipFile(file=origin, mode="r")
    new_zip = zipfile.ZipFile(file=target, mode="w", compression=zipfile.ZIP_DEFLATED, compresslevel=0 if is_jij else 9, allowZip64=False)
    for file in zip.filelist:
        if file.is_dir():
            new_zip.mkdir(file.filename)
        else:
            data = zip.open(file.filename)
            if file.filename.endswith(".jar"):
                hissboomify_zip(data, new_zip.open(file.filename, "w"), True)
            else:
                new_zip.open(file.filename, "w").write(data.read())
    zip.close()

parser = argparse.ArgumentParser()
parser.add_argument("path", help="The path of the files you want to detonate")
args = parser.parse_args()

for path in glob.glob(args.path):
    input = open(file=path, mode="r+b").read()
    input_temp = tempfile.TemporaryFile()
    input_temp.write(input)

    output = open(file=path, mode="w+b")
    hissboomify_zip(input_temp, output, False)
