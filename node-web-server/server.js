const express = require('express')
const app = express()
const port = 1113

app.use(express.json())

let data = [
    { id: 1, name: 'Alex', course: 'Computer Science'},
    { id: 2, name: 'John', course: 'Electrical'},
    { id: 3, name: 'Charlie', course: 'Civil'},
    { id: 4, name: 'JohnWick', course: 'Mathematics' },
    { id: 5, name: 'Jane', course: 'Physics' },
    { id: 6, name: 'Smith', course: 'Chemistry' }
]

//Read all items
app.get('/get', (req, res) => {
    res.json(data)
})

//Read specific item
app.get('/get/:id', (req, res) => {
    const id = parseInt(req.params.id)
    const item = data.find( i => i.id === id)
    if(item){
        res.json(item)
    } else{
        res.status(404).send('Item Not Fund')
    }
})

//Create a new item
app.post('/post', (req, res) => {
    const newItem =  { id: data.length + 1, ...req.body}
    data.push(newItem)
    res.status(201).json(newItem)
})

//Update an item
app.put('/update/:id', (req, res) => {
    const id = parseInt(req.params.id);
    const index = data.findIndex(i => i.id === id);
    if (index !== -1) {
      data[index] = { id, ...req.body };
      res.json(data[index]);
    } else {
      res.status(404).send('Item not found');
    }
});

// Delete an item
app.delete('/delete/:id', (req, res) => {
    const id = parseInt(req.params.id);
    const index = data.findIndex(i => i.id === id);
    if (index !== -1) {
      const deletedItem = data.splice(index, 1);
      res.json(deletedItem[0]);
      res.send(`Item deleted`);
    } else {
      res.status(404).send('Item not found');
    }
});

app.get('/', (req, res) => {
  res.send(`Server running successfully`);
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}/`)
})