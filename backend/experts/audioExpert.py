from birdnetlib import Recording
from birdnetlib.analyzer import Analyzer


class AudioExpert:
    def __init__(self):
        self.analyzer = Analyzer()

    def analyze_audio(self,file_path,min_conf=0.1):
        print(f"[AudioExpert] Analyzing file at: {file_path}") 

        recording = Recording(
            self.analyzer,
            file_path,
            min_conf=min_conf
        )

        recording.analyze()
        detections = recording.detections

        if(len(detections) == 0):
            print("Bird couldn't be detected through audio")
            return None

        highest_confidence = max(detections, key=lambda x: x["confidence"]) #returns the common_name of identification with the highest confidence level
        return highest_confidence["common_name"]
