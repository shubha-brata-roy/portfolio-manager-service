document.referrerPolicy = "no-referrer-when-downgrade";

window.addEventListener("load", (event) => {
    fetchStockData();
});


async function getStockData() {
    const promise = await fetch(`http://localhost:8080/stocks/instruments/current-price`, {
        mode: 'cors',
        credentials: 'include'
    });
    return await promise.json();
}

async function fetchStockData() {
    const data = await getStockData();
    // console.log(data);
    const tableBody = document.getElementById('stockTable').getElementsByTagName('tbody')[0];
    data.forEach(stock => {
        // console.log(`${stock.stockInstrumentName} - ${stock.currentPrice}`);
        const row = tableBody.insertRow();
        row.insertCell(0).innerHTML = `<input type="text" value="${stock.stockInstrumentName}" />`;
        row.insertCell(1).innerHTML = `<input type="text" value="${stock.currentPrice}" />`;
        row.insertCell(2).innerHTML = `<button onclick="deleteRow(this)">Delete</button>`;
    });
}

function addRow() {
    const tableBody = document.getElementById('stockTable').getElementsByTagName('tbody')[0];
    const row = tableBody.insertRow();
    row.insertCell(0).innerHTML = `<input type="text" value="" />`;
    row.insertCell(1).innerHTML = `<input type="text" value="" />`;
    row.insertCell(2).innerHTML = `<button onclick="deleteRow(this)">Delete</button>`;
}

function deleteRow(button) {
    const row = button.parentNode.parentNode;
    row.parentNode.removeChild(row);
}

async function submitData() {
    const tableBody = document.getElementById('stockTable').getElementsByTagName('tbody')[0];
    const rows = tableBody.getElementsByTagName('tr');
    const data = [];

    for (let i = 0; i < rows.length; i++) {
        const cells = rows[i].getElementsByTagName('td');
        const stock = {
            stockInstrumentName: cells[0].getElementsByTagName('input')[0].value,
            currentPrice: cells[1].getElementsByTagName('input')[0].value
        };
        data.push(stock);
    }

    const response = await fetch(`http://localhost:8080/stocks/instruments/current-price`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    
    if(response.ok) {
        // Convert data to worksheet
        const worksheet = XLSX.utils.json_to_sheet(data);

        // Create a new workbook and append the worksheet
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, 'Stocks');

        // Generate Excel file and trigger download
        XLSX.writeFile(workbook, 'stock-prices-update-complete.xlsx');
    } else {
        console.error('Failed to submit data:', response.statusText);
    }

}