var socket = io.connect('http://localhost:3000', {'forceNew': true})


socket.on('validado', function(data){
    console.log(data);
    render(data);
})

socket.on('addUser', data =>{
    console.log('user added: ' + data);
    renderUserAdd(data);
})

function renderUserAdd(data){
    var html =(`<div id="userAdded">
        <h4>Usuario agregado: ${data}<h4>
    </div>`)
    document.getElementById('addUser').innerHTML = html;
}
function render(data){
    var html = (
        `<div id="validation">
            <h4>Usuario aceptado: ${data}</h4>
        </div>`
    )
    document.getElementById('validation').innerHTML = html;
}

function validateUser(e){
    var data = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    console.log('Emitting new message');
    socket.emit('new-message', data);
    return false;
}

function addUser(e){
    var data = {
        username: document.getElementById('username2').value,
        password: document.getElementById('password2').value
    };
    console.log('hola');
    console.log('Adding new user' + data);
    socket.emit('addUser', data);
    return false;
}

function persistir(e){
    socket.emit('persistir', 'persistir');
    return false;
}