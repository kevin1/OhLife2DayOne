# Migrate OhLife entries to Day One

## Rationale
There are a lot of programs to migrate a text file exported from OhLife to Day One, but none of them did exactly what I want. For example, most of them would insert two line breaks because OhLife uses `\n\r` when exporting, and not just `\n`. (While this didn't make a difference in Day One's Markdown parsing, it still bothered me.)

## Usage
1. [Export your OhLife entries.](http://ohlife.com/export)
2. Install [Day One](http://dayoneapp.com).
3. Install the [Day One CLI](https://dayone.zendesk.com/hc/en-us/articles/200258954-Day-One-Tools).
4. Run this program!

```bash
cd OhLife2DayOne
javac *.java
java OhLife2DayOneTester /path/to/ohlife_20131031.txt "10:00:00 PM"
```

Note: OhLife doesn't save the time of entries, but Day One does. Use the second argument to specify the time to set in Day One when importing entries.

## Questions?
@kevinchen on Twitter
