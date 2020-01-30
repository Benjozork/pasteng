const ws = new WebSocket('ws://localhost:8080/');

ws.addEventListener('message', (e: any) => {
    const text = e.data;

    if (text.startsWith('pasteng:created')) {
        document.querySelector('#ws-send-result').innerHTML = `Response: ${e.data}`;
    } else if (text.startsWith('pasteng:ok')) {
        document.querySelector('#ws-receive-result').innerHTML = `Response: ${e.data}`;
    }
});

document.querySelector('#ws-send-button').addEventListener('click', () => {
    const content = (<HTMLInputElement> document.querySelector("#ws-send-text")).value;

    ws.send(`pasteng:new ${content}`);
});

document.querySelector('#ws-receive-button').addEventListener('click', () => {
    const content = (<HTMLInputElement> document.querySelector("#ws-receive-text")).value;

    ws.send(`pasteng:open ${content}`);
});
