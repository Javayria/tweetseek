from dataclasses import dataclass, field
from typing import Optional

@dataclass 
class FormResponseDTO:
    birdName : Optional[str] = field(default = None)
    expert: Optional[str] = field(default = None)
    birdImage: Optional[str] = field(default = None)
    imageFound: bool = field(default=False)