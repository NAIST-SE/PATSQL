#!/usr/bin/env python3

groups_and_ids = {
    "dev_set": ["001R", "002M", "003MR", "004R", "005", "006M", "007MR", "008", "009R", "010", "011R", "012", "013", "014", "015", "016", "017", "018", "019M", "020", "021", "022", "023", "024", "025", "026M", "027R", "028", "029", "030", "031", "032X", "033", "034", "035", "036", "037", "038", "039A", "040", "041", "042", "043M", "045", "046AM", "047M", "048", "050", "051", "052", "053AM", "054", "055AM", "056A", "057", "058M", "059M", "060", "062", "063A", "065M", "066", "067X", "068"],
    "top_rated_posts": ["001", "002", "003", "004", "005", "006", "007M", "008", "009", "010M", "011", "012", "013", "014", "015X", "016", "017", "018M", "019", "020X", "021M", "022X", "023X", "024X", "025X", "026X", "027", "028", "029", "030M", "031", "032", "033X", "034", "035X", "036", "037", "038", "039", "040", "041X", "042X", "043M", "044", "045", "046X", "047", "048", "049", "050X", "051", "052X", "053X", "054", "055", "056X", "057"],
    "recent_posts": ["001", "002X", "003", "004A", "005M", "006X", "007", "008X", "009", "010X", "011", "012", "013", "014", "015X", "016M", "017A", "018X", "019", "020X", "021M", "022M", "023X", "024X", "025A", "026X", "027X", "028", "029X", "030X", "031", "032M", "033X", "034", "035X", "036", "037X", "038", "039A", "040", "041X", "042", "043X", "044M", "045", "046X", "047X", "048XX", "049X", "050", "051"],
    "sqlsynthesizer": ["forum-questions-1", "forum-questions-2", "forum-questions-3", "forum-questions-4", "forum-questions-5", "textbook_5_1_1", "textbook_5_1_2X", "textbook_5_1_3A", "textbook_5_1_4X", "textbook_5_1_5X", "textbook_5_1_6", "textbook_5_1_7", "textbook_5_1_8", "textbook_5_1_9A", "textbook_5_1_10", "textbook_5_1_11", "textbook_5_1_12X", "textbook_5_2_1", "textbook_5_2_2", "textbook_5_2_3X", "textbook_5_2_4X", "textbook_5_2_5", "textbook_5_2_6", "textbook_5_2_7X", "textbook_5_2_8X", "textbook_5_2_9", "textbook_5_2_10X", "textbook_5_2_11X"]
}

def snake_to_camel(snake_str):
    components = snake_str.split("_")
    return components[0] + "".join(w.title() for w in components[1:])

def dehyphenate(hyphenated_str):
    return hyphenated_str.replace("-", "")

# below, { and } are escaped by doubling: {{ and }}.
# a backslash is used to escape newline
parse_test_snippet = '''\
	@Test
	void {GROUP_CAMEL}{ID_TITLE}ParseTest() {{
		Utils.loadFromScytheFile(new File("examples/scythe_mod/{GROUP}/{ID}"));
	}}
'''

for pair in groups_and_ids.items():
    group = pair[0]
    for id in pair[1]:
        print(parse_test_snippet.format(GROUP_CAMEL=snake_to_camel(group), ID_TITLE=dehyphenate(id), GROUP=group, ID=id))

synthesis_test_snippet = '''\
	@Test
	void {GROUP_CAMEL}{ID_TITLE}SynthesisTest() {{
		synthesizeFromScytheFile(new File("examples/scythe_mod/{GROUP}/{ID}"));
	}}
'''

disabled_synthesis_tests = {
    ("dev_set", "002M"),
    ("dev_set", "012"), # needs concatenation as a aggregation function.
    ("dev_set", "032X"),
    ("dev_set", "039A"),
    ("dev_set", "050"),
    ("dev_set", "053AM"),
    ("dev_set", "063A"),
    ("dev_set", "067X"),
    ("dev_set", "068"),
    ("top_rated_posts", "001"),
    ("top_rated_posts", "003"), # null
    ("top_rated_posts", "011"),
    ("top_rated_posts", "013"),
    ("top_rated_posts", "014"),
    ("top_rated_posts", "015X"),
    ("top_rated_posts", "016"),
    ("top_rated_posts", "018M"),
    ("top_rated_posts", "020X"), # null
    ("top_rated_posts", "023X"),
    ("top_rated_posts", "024X"),
    ("top_rated_posts", "025X"),
    ("top_rated_posts", "026X"),
    ("top_rated_posts", "028"),
    ("top_rated_posts", "029"),
    ("top_rated_posts", "030M"),
    ("top_rated_posts", "031"),
    ("top_rated_posts", "033X"),
    ("top_rated_posts", "035X"),
    ("top_rated_posts", "040"),
    ("top_rated_posts", "041X"),
    ("top_rated_posts", "042X"),
    ("top_rated_posts", "044"),
    ("top_rated_posts", "046X"),
    ("top_rated_posts", "047"),
    ("top_rated_posts", "049"),
    ("top_rated_posts", "050X"),
    ("top_rated_posts", "051"),
    ("top_rated_posts", "052X"),
    ("top_rated_posts", "053X"), # null
    ("top_rated_posts", "054"),
    ("top_rated_posts", "055"),
    ("top_rated_posts", "056X"),
    ("recent_posts", "001"),
    ("recent_posts", "002X"),
    ("recent_posts", "006X"),
    ("recent_posts", "008X"),
    ("recent_posts", "010X"),
    ("recent_posts", "011"),
    ("recent_posts", "012"),
    ("recent_posts", "014"),
    ("recent_posts", "015X"),
    ("recent_posts", "019"),
    ("recent_posts", "020X"),
    ("recent_posts", "021M"),
    ("recent_posts", "023X"),
    ("recent_posts", "024X"),
    ("recent_posts", "026X"), # null
    ("recent_posts", "027X"),
    ("recent_posts", "028"),
    ("recent_posts", "029X"),
    ("recent_posts", "030X"),
    ("recent_posts", "032M"),
    ("recent_posts", "033X"),
    ("recent_posts", "035X"),
    ("recent_posts", "037X"),
    ("recent_posts", "039A"),
    ("recent_posts", "041X"),
    ("recent_posts", "043X"),
    ("recent_posts", "048XX"),
    ("recent_posts", "049X"),
    ("recent_posts", "050"),
    ("recent_posts", "051"),
    ("sqlsynthesizer", "textbook_5_1_3A"),
    ("sqlsynthesizer", "textbook_5_1_4X"),
    ("sqlsynthesizer", "textbook_5_1_6"),
    ("sqlsynthesizer", "textbook_5_1_7"), # needs avg taking and returning Int.
    ("sqlsynthesizer", "textbook_5_1_8"), # needs avg taking and returning Int.
    ("sqlsynthesizer", "textbook_5_1_12X"),
    ("sqlsynthesizer", "textbook_5_2_3X"),
    ("sqlsynthesizer", "textbook_5_2_4X"),
    ("sqlsynthesizer", "textbook_5_2_5"),
    ("sqlsynthesizer", "textbook_5_2_7X"),
    ("sqlsynthesizer", "textbook_5_2_8X"),
    ("sqlsynthesizer", "textbook_5_2_9"),
    ("sqlsynthesizer", "textbook_5_2_11X"),
}

for pair in groups_and_ids.items():
    group = pair[0]
    for id in pair[1]:
        if (group, id) in disabled_synthesis_tests:
            print("\t@Disabled")
        print(synthesis_test_snippet.format(GROUP_CAMEL=snake_to_camel(group), ID_TITLE=dehyphenate(id), GROUP=group, ID=id))
