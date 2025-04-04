from dataclasses import dataclass, field
from typing import Optional


@dataclass 
class ExpertResponses:
    GeminiResponse : Optional[str] = field(default = None)
    AudioResponse: Optional[str] = field(default = None)
    ContextualResponse: Optional[str] = field(default = None)