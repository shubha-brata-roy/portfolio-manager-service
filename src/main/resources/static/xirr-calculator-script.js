document.addEventListener('DOMContentLoaded', function() {
    fetch('http://localhost:8080/metrics/instruments')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('metrics-table-body');
            data.forEach(item => {
                const row = document.createElement('tr');

                const nameCell = document.createElement('td');
                nameCell.textContent = item.name;
                row.appendChild(nameCell);

                const categoryCell = document.createElement('td');
                categoryCell.textContent = item.category;
                row.appendChild(categoryCell);

                const generateSheetCell = document.createElement('td');
                const generateSheetButton = document.createElement('button');
                generateSheetButton.textContent = 'Generate Sheet';
                generateSheetButton.addEventListener('click', () => {
                    fetch('http://localhost:8080/metrics/generate-sheet', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(item)
                    })
                    .then(response => {
                        if(response.ok) {
                            return response.blob();
                        } else {
                            throw new Error('Network response failed');
                        }
                    })
                    .then(blob => {
                        const url = window.URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.style.display = 'none';
                        a.href = url;
                        a.download = 'xirr-calculation.xlsx';
                        document.body.appendChild(a);
                        a.click();
                        window.URL.revokeObjectURL(url);
                        // a.remove();
                    })
                    .catch(error => {
                        console.error('There was a problem with the fetch operation: ', error);
                    });
                });
                generateSheetCell.appendChild(generateSheetButton);
                row.appendChild(generateSheetCell);

                const xirrCell = document.createElement('td');
                const xirrInput = document.createElement('input');
                xirrInput.type = 'number';
                xirrInput.step = '0.01';
                xirrCell.appendChild(xirrInput);
                row.appendChild(xirrCell);

                const actionCell = document.createElement('td');
                const actionButton = document.createElement('button');
                actionButton.textContent = 'o';
                actionButton.classList.add('action-button');
                actionButton.addEventListener('click', () => {
                    item.xirr = xirrInput.value;
                    fetch('http://localhost:8080/metrics/submit-xirr', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(item)
                    })
                    .then(response => {
                        if (response.ok) {
                            actionButton.textContent = '✔';
                            actionButton.style.backgroundColor = 'green';
                            actionButton.classList.add('submitted');
                        }
                    });
                });
                actionCell.appendChild(actionButton);
                row.appendChild(actionCell);

                tableBody.appendChild(row);
            });
        });

        // Add event listener for "Generate Master Sheet" button
    document.getElementById('generate-master-sheet-btn').addEventListener('click', async () => {
        try {
            const response = await fetch('http://localhost:8080/metrics/generate-master-sheet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'master_sheet.xlsx'; // Set the desired file name
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Error generating master sheet:', error);
        }
    });
});

