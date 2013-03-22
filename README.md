<!-- -*- markdown -*- -->
# `splain`

## Intelligent Error Messages for Java

### February 18, 2013

### [Laird Nelson][1]

`splain` is a small project that matches [`objexj`][2] [`Pattern`][7]s
against `List`s of `Object`s to produce rich error messages.

`splain` has two fundamental concepts:

 1. **Message selector.** A message selector is a key into a
    particular [`ResourceBundle`][3] (that also identifies what base
    [`ResourceBundle`][3] it belongs to).  It is represented in code
    by the [`ResourceBundleKey`][8] class.
    
 2. **Message catalog.** A message catalog is an ordered list of
    [`objexj`][2] [`Pattern`][7]s and message selectors that certain
    of those [`Pattern`][7]s, when matched, will cause to be selected.
    It is represented in code by the [`MessageFactory`][9] class.

`splain` unites these two concepts so that when you hand it a `List`
of `Object`s and a message catalog, it will return an appropriate
message selector.  From the message selector you can
[get a value out of a `ResourceBundle`][4].

### Sample Message Catalog

    # (Line comments like this one start with the hash (#) character.
    # objexj patterns are separated from corresponding message selectors
    # by a double-dash.
    java.lang.String(toString() == "a")/java.lang.String(toString() == "b")$
    ^java.lang.String(toString() == "c")
    --
    com.foobar.Bizbaw/sampleKey
    
In the sample message catalog above, `splain` will attempt to match
the first pattern against a user-supplied `List` of `Object`s (the
pattern says that a match will occur if the `List` ends with a
sequence of two strings, "`a`" and "`b`").  If that fails, then
`splain` will attempt to match the next pattern against the list.  If
either pattern succeeds, then the corresponding message selector
(`com.foobar.Bizbaw/sampleKey`) is used to identify a
[`ResourceBundle`][3] and a key inside it.

This means that if we use `splain` to find a message selector for
`Arrays.asList("x", "y", "z")` it will fail, but if we supply it with
either `Arrays.asList("c")` or `Arrays.asList("a", "b", "c")` it will
return a message selector that picks out the `sampleKey` key in the
[`ResourceBundle`][3] named by the bundle name of `com.foobar.Bizbaw`.

### Dereferencing Message Selectors

A message selector is (as mentioned previously) represented in code by
the [`ResourceBundleKey`][8] class.  It combines a bundle name with a
key that should be located in a [`ResourceBundle`][3] with that bundle
name.  A [`ResourceBundleKey`][8] is therefore capable of returning an
`Object` from its associated [`ResourceBundle`][4].

The resulting `Object`, if it is a `String`, is treated as an
[MVEL][5] [template][6], and is interpolated using [variables set][10]
in our `objexj` [pattern][7]s!

### A More Realistic Example

Here is an example that 

[1]: http://about.me/lairdnelson
[2]: http://ljnelson.github.com/objexj
[3]: http://docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.html
[4]: http://docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.html#getObject(java.lang.String)
[5]: http://mvel.codehaus.org/
[6]: http://mvel.codehaus.org/MVEL+2.0+Templating+Guide
[7]: http://ljnelson.github.com/objexj/apidocs/com/edugility/objexj/Pattern.html
[8]: apidocs/com/edugility/splain/ResourceBundleKey.html
[9]: apidocs/com/edugility/splain/MessageFactory.html
[10]: http://ljnelson.github.com/objexj/apidocs/com/edugility/objexj/Matcher.html#getVariables()
