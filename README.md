# BuildABrowser Browser

This README is for the Linux/Windows/Fallback version of BuildABrowser Browser.
Looking for Android? See [the Android version](https://github.com/BuildABrowser/BuildABrowser-Android).

BuildABrowser Browser is a browser and rendering engine written in Java.

The HTML and CSS parsers, style matcher, and layout engines are written 'from scratch' (though they do rely on some dependencies like SparseBitSet, Skija, and HarfBuzz).

This repository is a mono-repo containing most but not all of the components of BuildABrowser. Some components, such as the
[AccessKit Fork FFM module](https://github.com/BuildABrowser/AccessKit4J) and the [argument parser](https://github.com/BuildABrowser/JFlags)
are in their own dedicated repositories.

In the future, I intend to also write a guide to rebuilding some of BuildABrowser
(a few steps already exist on the website, but progress on this front is not far along yet).

## What are the features of BuildABrowser Browser?

The browser has support for the following:
* Runs on Linux, Windows, and (experimental) Android Desktop
* Layouts: flow/flow-root, flex, grid, table, inline variants of all prior listed
  - Note: subgrid is not supported. Some alignment properties present in flex are missing from grid.
  - Note: Table only supports `table-layout: auto;` (it had briefly supported `fixed`, but that was removed).
* Positioning Schemes: static, relative, absolute, sticky, fixed
* Floating and cleared elements
* CSS Combinators: Child, Descendant, Next-Sibling, Subsequent-Sibling
* CSS Selectors: ID, type, class, attribute
* CSS Pseudo-Classes/Elements: :hover,  :link, :focus, :focus-within,
:focus-visible, :has, :is, :not, :where, :first-child, :last-child, :only-child,
:first-of-type, :last-of-type, :only-of-type, ::before, ::after
* CSS Functions: calc (and related functions), var
* CSS layers (the `@layer` at-rules)
* Media Queries: screen type, min/max-width/height media features
* Text selection, text fields, GET forms, cycle focus via tab button, and fragment navigation
* (Linux) Support for A11Y via a fork of AccessKit (Experimental, enable with `-a11y accesskit`). Designed for Orca - other tools may not work.
* (Linux/Windows/fallback) Includes a utility to view a page's DOM tree
* (Linux/Windows) Skija (Skia) and Java2D graphics backends, HttpClient network backend
* (Android Desktop) android.graphics graphics backend, OkHttp network backend

## Running BuildABrowser Browser

If you downloaded BuildABrowser Browser as a release jarfile, you can run:
```bash
java -jar browser.jar
```
(Replace `browser.jar` with the path to your downloaded copy of the browser.)

When run from a jarfile, BuildABrowser Browser will relaunch itself with additional sets of flags. If the auto-relaunch causes problems, you can pass the `--no-relaunch` flag.

To view all flags and arguments that you can pass while running BuildABrowser Browser, pass the `-h` flag (`java -jar browser.jar -h`)

## Testing BuildABrowser Browser in Gradle

Clone the BuildABrowser repository:
```bash
git clone https://github.com/BuildABrowser/BuildABrowser.git
cd BuildABrowser
```

Standard Gradle commands are available:
* `./gradlew run --args="https://example.com/` - Run BuildABrowser Browser, and open `example.com` in the new tab page
* `./gradlew build` - Bundles building, testing, and a few other checks into the same command. A jarfile will be output to `./Browser/build/libs/Browser-0.1.0-all.jar`.

Because BuildABrowser Browser is a multi-modular project, the tests maybe be split across
build folders. Gradle should tell you where you can find a report for failing
builds.

## Can I embed the renderer in my Swing or "Jetpack Compose for Android" project?

Yes. See the embedding guides for [Swing](https://github.com/BuildABrowser/example-embedding-swing) and [JetPack Compose for Android](https://github.com/BuildABrowser/example-embedding-android).

The Android version is only intended for Android Desktop environments.

### Does BuildABrowser Browser support scripting?

Not at this time.

## Why build a browser and its rendering engine?

I like browsers.

## Why Java?

Writing a browser is a large project, and adding memory management and platform-specific calls on top of that would further slow down progress.

I would rather be able to quickly cover a larger web surface area than write code that squeezes performance out of the machine but remains stuck in an early stage.

Regardless, modern Java is actually quite fast, and BuildABrowser Browser is capable of loading large pages like The Vulkan Specification in seconds on my machine (4s to first paint, 13s to ready).

## What are the goals for BuildABrowser Browser in the future?

There is a lot to do still!

Doing everything will span **years**, so don't expect anything here anytime soon.

* Take a break so I don't burn out
* There is an ongoing `text-refactor` branch that needs to be finished and merged in
* Need to catch up writing unit tests, it is way behind
* Continue fixing bugs for various websites
* Implement visual polish (underlines, italics, opacity, transforms, gradients, border radius, etc)
* Implement CSS animations
* Refactor the API of various modules to be more ipc-friendly, then implement multi-process support
  - Because the browser is very modular, this isn't impossible, but will still take a good deal of effort
* Write a WebIDL binding layer, integrate a JS engine, and start working on some simple web APIs
  - I plan to use GraalJS instead of writing one from scratch, at least for the beginning
* Support BIDI and rtl writing directions, support vertical writing modes
  - I don't want to know how hard this will be...
* Implement the fragmentation spec and allow page breaks
* Implement a PDF backend and a print dialogue