const socket = require('socket.io');
const fs = require('fs');
const util = require('util');
var users =[{
    username: "Michael",
    password: "123"
}];

var usuariosValidados =[];
var time =0;

function startConnection(server){

    var io = socket(server);
    io.on('connection', socket =>{
        console.log('New connection to socket');
        console.log('inicion', "Conexión establecida");

        socket.on('new-message', function(data){
            var inicial = Date.now();
            //data = JSON.parse(data);
            console.log('new validation');
            var validate = false;
            for(user of users){
                if(user.username == data.username){
                    if(user.password == data.password){
                        validate = true;
                    }
                }
            }
            console.log('About to emit: ' + validate);
            var final = Date.now();
            var total = final - inicial;
            var usuarioValidado = "UserName: " + data.username + " validado: " + validate;
            usuariosValidados.push(usuarioValidado);
            console.log("Tiempo de procesamiento; " + total + " ms");
            time += total;
            console.log("Tiempo promedio utilizado para " + usuariosValidados.length + " usuarios: " + time/usuariosValidados.length + " ms");
            socket.emit('validado',validate);
            
        })

        socket.on('addUser', data =>{
            console.log('adding new user');
            users.push(data);
            socket.emit('addUser', true);
            var user = "nombre: " + data.username + " password: " + data.password;
        })

        socket.on('persistir', data =>{
            for(usuario of usuariosValidados){
                persistir(1, usuario);
            }

            for(usuario of users){
                persistir(2, usuario);
            }
        })
    })
}

function persistir(tipo, usuarioValidado){
    if(tipo == 1){
        fs.appendFile("./JSON/usersValidados.json", util.inspect(usuarioValidado) + "\r\n", err =>{
            if(err){
                return console.log(err);
            }
        });
    }else{
        fs.appendFile("./JSON/users.json", util.inspect(usuarioValidado) + "\r\n", err =>{
            if(err){
                return console.log(err);
            }
        });
    }
}
module.exports.startConnection = startConnection;