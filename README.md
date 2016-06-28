# dr-nlp

This repository provides API's that provides following features:

1. Reads text from text file and separate sentence by sentence boundary
2. It tokenizes the text in the file
3. Identifies proper nouns in each sentence based on the list provided in another file and outputs data in XML Java Object.
4. Reads zip file, extracts it and process each file in it concurrently. It prints XML representation for each file in zip.

Limitations:

Right now these API's works on predefined .txt files in resource folder as input

Improvements:

Searching for nouns in each sentence can be improved using binary tree search or some other effective way. 

