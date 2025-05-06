const WebSocket = require('ws');

const ws = new WebSocket('ws://localhost:8080/ws/game');

ws.on('error', console.error);

ws.on('open', function open() {
    ws.emit('message', 'Message event body payload');
    ws.emit('custom', 'Custom event body payload');
    ws.send('[Default]');
});

ws.on('message', function message(data) {
    console.log('received: %s', data);
});

