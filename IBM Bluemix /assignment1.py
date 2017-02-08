import os
import swiftclient
import Crypto
import os, random, struct
from Crypto.Cipher import AES

auth_url="https://identity.open.softlayer.com/v3"
project= "object_storage_ef872a8c_5f1b_44bb_a89b_99c4c6fbbc79"
projectId= "1b9c5a938cb946d5904b3c275386c587"
region= "dallas"
userId= "0a1661df4eb74a7f8b698ba1d34510cc"
username=  "admin_53e6f58786ee0ad08f3e4d6a9c3c1c70b61d1aca"
password= "s~p4PxB3..OtGAI6"

key = 'aSSSSSaSSaaSSSaS'


conn = swiftclient.Connection(key=password,user=username,authurl=auth_url,auth_version='3',
os_options={"project_id": projectId,"user_id": userId,"region_name": region})

acc =  conn.get_account()[1]


container_name = 'new-container'

num = int(raw_input('Enter Choice'))


def encrypt_file(key, in_filename, out_filename=None, chunksize=64 * 1024):
    print "encrypting file"
    if not out_filename:
        out_filename = in_filename + '.enc'
        print out_filename
    iv = ''.join(chr(random.randint(0, 0xFF)) for i in range(16))
    encryptor = AES.new(key, AES.MODE_CBC, iv)
    filesize = os.path.getsize(in_filename)

    with open(in_filename, 'rb') as infile:
        with open(out_filename, 'wb') as outfile:
            outfile.write(struct.pack('<Q', filesize))
            outfile.write(iv)

            while True:
                chunk = infile.read(chunksize)
                if len(chunk) == 0:
                    break
                elif len(chunk) % 16 != 0:
                    chunk += ' ' * (16 - len(chunk) % 16)

                outfile.write(encryptor.encrypt(chunk))


def decrypt_file(key, in_filename, out_filename=None, chunksize=24*1024):

    if not out_filename:
        out_filename = os.path.splitext(in_filename)[0]

    with open(in_filename, 'rb') as infile:
        origsize = struct.unpack('<Q', infile.read(struct.calcsize('Q')))[0]
        iv = infile.read(16)
        decryptor = AES.new(key, AES.MODE_CBC, iv)

        with open(out_filename, 'wb') as outfile:
            while True:
                chunk = infile.read(chunksize)
                if len(chunk) == 0:
                    break
                outfile.write(decryptor.decrypt(chunk))

            outfile.truncate(origsize)
            with open(file_name, 'w') as my_example:
                my_example.write(obj[1])
                print "\nObject %s downloaded successfully." % file_name
    file = open(file_name, 'r')
    print file.read()

if num == 0:
    conn.put_container(container_name)
    file_name = raw_input('Enter File name to be uploaded')
    encrypt_file(key,file_name)


    # file_name = '/Users/parthshah/Desktop/example_file.txt'


    with open(file_name+'.enc', 'r') as example_file:
        conn.put_object(container_name,
        file_name,contents= example_file.read(),content_type='text/plain')

    for container in conn.get_account()[1]:
        print container['name']

    for container in conn.get_account()[1]:
        for data in conn.get_container(container['name'])[1]:
            print 'object: {0}\t size: {1}\t date: {2}'.format(data['name'], data['bytes'], data['last_modified'])

    file = open(file_name, 'r')
    print file.read()



elif num == 1:
    file_name = raw_input('Enter File name to be downloaded')
    obj = conn.get_object(container_name, file_name)

    obj = conn.get_object(container_name,file_name)
    with open(file_name, 'w') as my_example:
        my_example.write(obj[1])
    decrypt_file(file_name)
    print "nObject %s downloaded successfully." % file_name
    print ("Done")


elif num == 3:
    file_name = raw_input('Enter File name to be Deleted')
    conn.delete_object(container_name, file_name)
    print "\nObject %s deleted successfully." % file_name




# http://eli.thegreenplace.net/2010/06/25/aes-encryption-of-files-in-python-with-pycrypto