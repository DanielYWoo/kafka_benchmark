const { Kafka } = require('kafkajs')

const kafka = new Kafka({
  clientId: 'my-kafkajs-app',
  brokers: ['localhost:9092', 'localhost:9093']
})
const loops = 1000000

const producer = kafka.producer()

const run = async () => {
  console.log('connecting')
  await producer.connect()
  console.log('connected')
  console.log(new Date())
  var t1 = new Date().getTime()
  for (var i = 0; i < loops; i++) {
    await producer.send({
      topic: 'test-topic',
      messages: [
        { value: 'hello ladskfj sadfljk asdfljk asdflj asdf asdf asdf asdf asdf sadf asdf asd fasd f asdf asd f' + i },
      ],
    })
    if (i % 1000 == 0) console.log('sent ', i);
  }
  console.log(new Date())
  var t2 = new Date().getTime()
  console.log('time consumed:', t2 - t1)
  console.log('TPS:', loops/(t2-t1) * 1000)
}

run().catch(console.error)
