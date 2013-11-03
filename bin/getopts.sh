#!/bin/bash

# getopts test for my standard type arguments 

while getopts dhir flag; do
  case $flag in
    d)
      echo "-d used";
      ;;
	h)
	  echo "-h used";
	  ;;
    i)
      echo "-i used";
      ;;
    r)
      echo "-r used";
      ;;
    ?)
      exit;
      ;;
  esac
done

shift $(( OPTIND - 1 ));
