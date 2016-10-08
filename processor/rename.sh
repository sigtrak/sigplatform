for file in *.log.DONE
do
  mv "$file" "${file/.log.DONE/.log}"
done