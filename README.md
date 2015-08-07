# edcmcgen

A small utility to generate a CH Control Manager cmc definition file from Elite Dangerous bindings.

## TODO

- The [dictionary from Elite key names to CH key names](src/edcmcgen/dictionary.clj) is far from complete. I will be adding them as I need them, please submit a pull request if you have improvements.

## Installation

Clone this repo.

## Usage

Currently you will need [Leiningen](http://leiningen.org/) to run it:

    $ lein run -- path/to/Custom.binds

To get a full list of options, run:

    $ lein run -- -h

## Code

Less ugly than in the beginning, sadly no TDD. But this is just a toy tool really.

## License

Copyright © 2015 Imre Kószó

Distributed under the MIT license.
