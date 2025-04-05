from dataclasses import dataclass

@dataclass(frozen=True) #frozen equals True means that the class cannot be mutated it is essentially a set of key value pairs
class BirdContext:
    size: str
    color: str
    location: str
