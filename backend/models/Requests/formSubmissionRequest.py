from dataclasses import dataclass, field
from typing import Optional

@dataclass 
class FormSubmissionRequest:
    base64Image: Optional[str] = field(default = None)
    base64Audio: Optional[str] = field(default = None)
    size: Optional[str] = field(default = None)
    color: Optional[str] = field(default = None)
    location: Optional[str] = field(default = None)
