#! /bin/bash
#
# Generates tsv archives for Fishbase and SeaLifeBase tables extracted from https://fishbase.ropensci.org 
# see http://fishbase.org and https://github.com/ropensci/fishbaseapi
#
function archive() {
  for table in $(cat table_names.tsv); do
    archiveTable "$table/fishbase"
    archiveTable "$table/sealifebase"
  done
}

function archiveTable() {
  table=$1
  tableNoSlash=${table//\//_}
  
  offset=0
  limit=5000
  returned=$limit

  data=$(mktemp)
  header=$(mktemp)

  while [ $returned -gt 0 ] 
  do
    filename=$(mktemp)
    url="https://fishbase.ropensci.org/$table?offset=$offset&limit=$limit" 
    echo "[$url] downloading..."
    curl --silent -H "Accept-Encoding: gzip" $url > $filename 
    echo "[$url] downloaded."
    returned=`cat $filename | gunzip | jq .returned`
    if [ $returned -gt 0 ]; then
      cat $filename | gunzip | jq -r '.data[0] | keys_unsorted | @tsv' | gzip > $header
      cat $filename | gunzip | jq -r '.data[] | map(tostring) | @tsv' | gzip >> $data
    fi
    offset=$[$offset+$limit]
    echo "returned: $returned, offset= $offset"
    rm $filename
  done
  archive="$tableNoSlash.tsv.gz"
  cat $header $data > $archive 
  rm $data $header
}

archive
