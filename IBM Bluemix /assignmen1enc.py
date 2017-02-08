import os
import swiftclient
import Crypto
import os, random, struct
from Crypto.Cipher import DES3
from Crypto import Random



auth_url = "https://identity.open.softlayer.com/v3"
project = "object_storage_ef872a8c_5f1b_44bb_a89b_99c4c6fbbc79"
projectId = "1b9c5a938cb946d5904b3c275386c587"
region = "dallas"
userId = "0a1661df4eb74a7f8b698ba1d34510cc"
username = "admin_53e6f58786ee0ad08f3e4d6a9c3c1c70b61d1aca"
password = "s~p4PxB3..OtGAI6"

conn = swiftclient.Connection(key=password, user=username, authurl=auth_url, auth_version='3',
                              os_options={"project_id": projectId, "user_id": userId, "region_name": region})

acc = conn.get_account()[1]


def upload_file(file_name, username):
    total_size = 0
    for data in conn.get_container('orignal')[1]:
        name = data['name'].split('.')
        length = len(name)
        if (name[length - 1] == username):
            total_size = total_size + data['bytes']

    file_size = os.path.getsize(file_name)

    if (total_size+file_size<1000000):
        flag = 1
        for data in conn.get_container('orignal')[1]:
            if (file_name + '.' + username == data['name']):
                obj = conn.get_object('orignal', file_name + '.' + username)
                with open(file_name+'.bkp', 'w') as my_example:
                    my_example.write(obj[1])
                    print "downloaded from orignal"
                with open(file_name+'.bkp', 'r') as example_file:
                    # file_name = file_name[:-4]
                    conn.put_object('backup',
                    file_name + '.' + username, contents=example_file.read(), content_type='text/plain')
                    print "placed backup"
                for data in conn.get_container('orignal')[1]:
                    with open(file_name, 'r') as example_file:
                        conn.put_object('orignal',
                                        file_name + '.' + username, contents=example_file.read(),
                                        content_type='text/plain')
                flag = 0

        if flag == 1:
            for data in conn.get_container('orignal')[1]:
                with open(file_name, 'r') as example_file:
                    conn.put_object('orignal',
                    file_name + '.' + username, contents=example_file.read(), content_type='text/plain')

            for container in conn.get_account()[1]:
                print container['name']

            for container in conn.get_account()[1]:
                for data in conn.get_container(container['name'])[1]:
                    print 'object: {0}\t size: {1}\t date: {2}'.format(data['name'], data['bytes'], data['last_modified'])
    else:
        print "Quota Over"

def download_file(file_name, container_name, username):
    obj = conn.get_object(container_name, file_name +'.'+ username)
    with open(file_name + '.' + username, 'w') as my_example:
        my_example.write(obj[1])
    print "nObject %s downloaded successfully." % file_name
    print ("Done")


def delete_file(file_name, container_name, username):
    conn.delete_object(container_name, file_name + '.enc'+'.' + username)
    print "\nObject %s deleted successfully." % file_name


def file_list(username,container_name):
    print ("\nObject List:")
    total_size=0
    for data in conn.get_container('orignal')[1]:
        name = data['name'].split('.')
        length =len(name)
        if(name[length-1]==username):
                total_size = total_size + data['bytes']
                print 'object: {0}\t size: {1}\t date: {2}'.format(data['name'], data['bytes'], data['last_modified'])
    print "Total size used in bytes"
    print total_size

def encrypt_file(in_filename, out_filename, chunk_size,iv,key):
    des3 = DES3.new(key, DES3.MODE_CFB, iv)
    with open(in_filename, 'r') as in_file:
        print("Encypting")
        with open(out_filename, 'w') as out_file:
            while True:
                chunk = in_file.read(chunk_size)
                if len(chunk) == 0:
                    break
                elif len(chunk) % 16 != 0:
                    chunk += ' ' * (16 - len(chunk) % 16)
                    out_file.write(des3.encrypt(chunk))




def del_restore(username):
    for data in conn.get_container('orignal')[1]:
        name = data['name'].split('.')
        length = len(name)
        if (name[length - 1] == username):
            conn.delete_object('orignal', data['name'])

    for data in conn.get_container('backup')[1]:
        if (name[length-1]==username):
            obj = conn.get_object('backup', data['name'])
            with open(data['name'], 'w') as my_example:
                my_example.write(obj[1])
        new_filename = data['name']

    for data in conn.get_container('orignal')[1]:
        if (name[length - 1] == username):
            with open(new_filename, 'r') as example_file:
                 conn.put_object('orignal',
                new_filename, contents=example_file.read(), content_type='text/plain')
        print "Backup file uploaded to orignal"






def size_greater(username, size):
    flag=1
    for data in conn.get_container('orignal')[1]:
        name = data['name'].split('.')
        length =len(name)
        if(name[length-1]==username):
            if (data['bytes'] > usersize):
                print '-> {0} t size: {1} t date: {2}'.format(data['name'], data['bytes'], data['last_modified'])
                flag=0
    if flag==1:
        print "No file found larger then entered size"



choice =1

while (choice):

    username =raw_input("Enter UserName")


    if username == 'parth':
        while choice:

            username = 'parth'
            select = int(raw_input("1. Upload a file \n2.Delete a file \n3.Download a file\n4.Show file list and size\n5.Enter a size\n6.Delete and Restore Backup files\n7.Exit"))

            if select == 1:
                filename = raw_input("\nEnter a filename to upload")
                iv = Random.get_random_bytes(8)
                encrypt_file(filename, filename +".enc", 8192,iv, '1234567812345678')
                os.remove(filename+'.enc')
                upload_file(filename+".enc", username)

            elif select == 2:
                filename = raw_input("\nEnter a filename to delete")
                delete_file(filename, 'orignal', username)
            elif select == 3:
                filename = raw_input("\nEnter a filename to download")
                download_file(filename, 'orignal', username)
                break
            elif select == 4:
                file_list(username,'orignal')
            elif select == 5:
                usersize = int(raw_input("Enter a size"))
                size_greater(username,usersize)
            elif select==6:
                del_restore(username)
            elif select ==7:
                break
    if username=='dikshant':
        while choice:

            username = 'dikshant'
            select = int(raw_input("1. Upload a file \n2.Delete a file \n3.Download a file\n4.Show file list and size \n5.Enter a size\n6.Delete and Restore Backup files\n7.Exit"))

            if select == 1:
                filename = raw_input("\nEnter a filename to upload")
                iv = Random.get_random_bytes(8)
                encrypt_file(filename, filename + ".enc", 8192, iv, '1234567812345678')
                upload_file(filename, username)
                os.remove(filename)
            elif select == 2:
                filename = raw_input("\nEnter a filename to delete")
                delete_file(filename, username)
            elif select == 3:
                filename = raw_input("\nEnter a filename to download")
                download_file(filename, 'orignal', username)
                break
            elif select == 4:
                file_list(username,'orignal')
                break;
            elif select == 5:
                usersize = int(raw_input("Enter a size"))
                size_greater(username, usersize)
            elif select == 6:
                del_restore(username)
            elif select == 7:
                break
    if username=='chawla':
        while choice:

            username = 'chawla'
            select = int(raw_input("1. Upload a file \n2.Delete a file \n3.Download a file\n4.Show file list and size\n5.Enter a size\n6.Delete and Restore Backup files\n7.Exit"))

            if select == 1:
                filename = raw_input("\nEnter a filename to upload")
                iv = Random.get_random_bytes(8)
                encrypt_file(filename, filename + ".enc", 8192, iv, '1234567812345678')
                upload_file(filename + ".enc", username)
            elif select == 2:
                filename = raw_input("\nEnter a filename to delete")
                delete_file(filename, 'orignal', username)
            elif select == 3:
                filename = raw_input("\nEnter a filename to download")
                download_file(filename, 'orignal', username)
                break
            elif select == 4:
                file_list(username, 'orignal')
                break
            elif select == 5:
                usersize = int(raw_input("Enter a size"))
                size_greater(username, usersize)
            elif select==6:
                del_restore(username)
            elif select ==7:
                break
    else:
        break



        # /Users/parthshah/Desktop/file2.txt
