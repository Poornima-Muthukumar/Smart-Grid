if [[ -z $1 ]]
then
    echo "Enter variance or par as an argument"
    exit 1
fi

mode=$1
java -cp bin server.Server server1 18 7 9 17 mode 127.0.0.1 6666 127.0.0.1 7777 127.0.0.1 8888 &
java -cp bin server.Server server2 18 7 23 8 mode 127.0.0.1 6666 127.0.0.1 7777 127.0.0.1 8888 &
java -cp bin server.Server server3 18 7 9 17 mode 127.0.0.1 6666 127.0.0.1 7777 127.0.0.1 8888 &
