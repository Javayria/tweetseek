import requests

from models.Requests.birdContext import BirdContext

class ContextualExpert():
    bird_map = {
        BirdContext("Small", "Red", "Forest"): "Ruby-crowned Kinglet",
        BirdContext("Small", "Blue", "Meadow"): "Eastern Bluebird",
        BirdContext("Small", "Brown", "Urban"): "House Wren",
        BirdContext("Small", "Black", "Desert"): "Black-throated Sparrow",
        BirdContext("Small", "White", "Wetlands"): "Snowy Egret",

        BirdContext("Medium", "Red", "Urban"): "Northern Cardinal",
        BirdContext("Medium", "Blue", "Forest"): "Steller's Jay",
        BirdContext("Medium", "Brown", "Meadow"): "Song Sparrow",
        BirdContext("Medium", "Black", "Wetlands"): "Red-winged Blackbird",
        BirdContext("Medium", "White", "Desert"): "White-crowned Sparrow",

        BirdContext("Large", "Red", "Wetlands"): "Sandhill Crane",
        BirdContext("Large", "Blue", "Urban"): "Great Blue Heron",
        BirdContext("Large", "Brown", "Forest"): "Great Horned Owl",
        BirdContext("Large", "Black", "Meadow"): "Turkey Vulture",
        BirdContext("Large", "White", "Desert"): "Snow Goose",

        BirdContext("Small", "Red", "Meadow"): "Vermilion Flycatcher",
        BirdContext("Medium", "Blue", "Wetlands"): "Little Blue Heron",
        BirdContext("Large", "Brown", "Urban"): "Red-tailed Hawk",
        BirdContext("Medium", "Black", "Forest"): "Common Grackle",
        BirdContext("Small", "White", "Desert"): "Horned Lark",

        BirdContext("Large", "Red", "Urban"): "Northern Flicker",
        BirdContext("Medium", "Brown", "Desert"): "Cactus Wren",
        BirdContext("Small", "Blue", "Desert"): "Pinyon Jay",
        BirdContext("Large", "White", "Wetlands"): "American White Pelican",
        BirdContext("Medium", "White", "Forest"): "Northern Shrike"
    }

    def analyze_contextual_data(self, size, color, location):
        key = BirdContext(size, color, location)
        
        if key in self.bird_map:
            return self.bird_map.get(key, None)
        
        best_match = None 
        best_score = 0

        for bird_context, bird_name in self.bird_map.items():
            score = 0
            if size and bird_context.size == size:
                score += 1
            if color and bird_context.color == color:
                score += 1
            if location and bird_context.location == location:
                score += 1

            #could change it to equal, if we want the furthest partially matched key in the map
            if score > best_score:
                best_score = score
                best_match = bird_name

        return best_match

