#!/usr/bin/env bash

if [ "$1" == '' ] || [ "$1" == '--help' ] || [ "$1" == '-help' ]; then
  echo ">>>>>>>>> USAGE <<<<<<<<<<"
  echo "Either fully run DiffDetective with dataset from dataset file at docs/datasets/eugen-bachelor-thesis.md."
  exit
fi
cd /home/sherlock || exit
  cd holmes || exit

echo "Running a the check."
java -cp DiffDetective.jar org.variantsync.diffdetective.experiments.views_es.Main

echo "Collecting results."
cp -r results/* ../results/
echo "The results are located in the 'results' directory."

