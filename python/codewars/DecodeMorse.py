#!/usr/bin/env python


# In this kata you have to write a simple Morse code decoder.
# While the Morse code is now mostly superceded by voice and digital data communication channels,
# it still has its use in some applications around the world.
# The Morse code encodes every character as a sequence of "dots" and "dashes".
#
# For example, the letter A is coded as ·−, letter Q is coded as −−·−, and digit 1 is coded as ·−−−−.
#
# The Morse code is case-insensitive, traditionally capital letters are used.
# When the message is written in Morse code, a single space is used to separate the character codes and 3 spaces are used to separate words.
# For example, the message HEY JUDE in Morse code is ···· · −·−−   ·−−− ··− −·· ·.
#
# NOTE: Extra spaces before or after the code have no meaning and should be ignored.
#
# In addition to letters, digits and some punctuation, there are some special service codes,
# the most notorious of those is the international distress signal SOS (that was first issued by Titanic), that is coded as ···−−−···.
# These special codes are treated as single special characters, and usually are transmitted as separate words.
#
# Your task is to implement a function that would take the morse code as input and return a decoded human-readable string.
#
# For example:
#
# decodeMorse('.... . -.--   .--- ..- -.. .')
# #should return "HEY JUDE"
# NOTE: For coding purposes you have to use ASCII characters . and -, not Unicode characters.
#
# The Morse code table is preloaded for you as a dictionary, feel free to use it:
#
# Coffeescript/C++/Go/JavaScript/PHP/Python/Ruby/TypeScript: MORSE_CODE['.--']
# C#: MorseCode.Get(".--") (returns string)
# Elixir: morse_codes variable
# Elm: MorseCodes.get : Dict String String
# Haskell: morseCodes ! ".--" (Codes are in a Map String String)
# Java: MorseCode.get(".--")
# Kotlin: MorseCode[".--"] ?: "" or MorseCode.getOrDefault(".--", "")
# Rust: self.morse_code
# Scala: morseCodes(".--")
#
# All the test strings would contain valid Morse code, so you may skip checking for errors and exceptions.

def decodeMorse(morse_code):
    # ToDo: Accept dots, dashes and spaces, return human-readable message
    morse_code.replace("·",MORSE_CODE["."]).replace('−', MORSE_CODE["-"])
    morse_code = morse_code.strip()
    if(morse_code == "...---..."):
        return "SOS"

    result = []
    words = morse_code.split("   ")
    for w in words:
        letters = w.split(" ")
        for l in letters:
            result.append(MORSE_CODE[l])
        result.append(" ")

    return "".join(result)
