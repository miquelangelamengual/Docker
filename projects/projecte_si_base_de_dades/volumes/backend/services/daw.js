const db = require('./db');
const helper = require('../helper');
const config = require('../config');

async function getMultiple(q){
  const rows = await db.query(q);
  const data = helper.emptyOrRows(rows);

  return {
    data
  }
}

module.exports = {
  getMultiple
}