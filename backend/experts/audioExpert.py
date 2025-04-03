from birdnetlib import Recording
from birdnetlib.analyzer import Analyzer


class AudioExpert:
    def __init__(self):
        self.analyzer = Analyzer()

    def analyze_audio(self,file_path,min_conf=0.1):

        recording = Recording(
            self.analyzer,
            file_path,
            min_conf=min_conf
        )
        recording.analyze()
        detections = recording.detections
        highest_confidence = max(detections, key=lambda x: x["confidence"]) #returns the common_name of identification with the highest confidence level
        return highest_confidence["common_name"]
