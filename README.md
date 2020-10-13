# Lab 3

This is Nicolas Robertson and Joe Walbran's code for lab 3, as part of the University of Minnesota Morris's CSCI 4554 Cryptography class (Fall 2020).

## To run

To run our program, make sure your have the plaintext you want to encrypt (or the ciphertext you want to decrypt) saved in a file. Then, run

```sh
javac Main.java && java Main
```

The program will prompt you for what to input!

## Part 3

We decrypted two ciphertexts from Ananya's group (see the files `ananyas-plaintext-MODE.txt`). We also encrypted two of our own plaintexts (see the files `our-ciphertext-ctr.txt` and `our-second-ciphertext-cfb.txt`). The keys and initialization vectors we used can be found in `our-key-and-iv.txt`.

## Part 4

We've provided an analysis of what happens when a single bit is flipped in the file `bit-error-observations.txt`. (See the files `our-plaintext-MODE-corrupted.txt` for the exact output!)