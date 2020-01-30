const ws = new WebSocket('ws://localhost:8080/');

document.querySelector('button').addEventListener('click', () => {
    ws.addEventListener('message', (e: any) => {
        document.querySelector('#ws-receive').innerHTML = `Response: ${e.data}`;
    });
    ws.send('pasteng:open');
});
