#!/bin/bash 
# wait-for-db.sh

set -e

host="$1"
shift 
cmd="$@"

echo '** Esperamos a que arranque completamente la base de datos **'

/bin/sleep 90

echo '** Suponemos que ya ha iniciado **'

exec $cmd
