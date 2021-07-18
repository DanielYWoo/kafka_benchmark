const apm = require('elastic-apm-node').start({
  serviceName: 'test-nodejs-express'
})

const app = require('express')()
const Redis = require("ioredis");
const redis = new Redis(); // uses defaults unless given configuration object


app.get('/', async (req, res) => {
  res.send('Hello World!')
})

app.get('/redis', async (req, res) => {
 await redis.set("foo", "bar")
 var result = await redis.get("foo")
 res.send('Hello Redis!' + result)
})

app.listen(3000)
