# Contributing to PowerMock #

First of all, thank you for considering contributing to PowerMock. Please, read the guideline before start. Following these guidelines helps to communicate that you respect the time of the developers managing and developing PowerMock.

## Content ##

* [If looking for support](#if-looking-for-support)
* [Pull request criteria](#pull-request-criteria)
* [General info](#general-info)
* [More on pull requests](#more-on-pull-requests)


## If looking for support ##

Search / Ask question on [stackoverflow](https://stackoverflow.com/questions/tagged/powermock)
Go to the PowerMock  [mailing-list](https://groups.google.com/forum/#!forum/powermock/) (moderated)
Issues should always have a [Short, Self Contained, Correct (Compilable)](http://sscce.org), Example (same as any question on stackoverflow.com)

## Pull request criteria ##
* At least one commit message in the PR starts with Fixes #id : where id is an [issue tracker](https://github.com/powermock/powermock/issues) id. This allows track release notes. Also GitHub will track the issue and [close it](https://github.com/blog/1386-closing-issues-via-commit-messages) when the PR is merged.

* Use `@since` tags for new public APIs

* Include tests

* Document public APIs with examples

* PowerMock provides two APIs: EasyMock and Mockito. If you add a new feature, please follow the same API style as the mocking framework which API you extend.

* For new features consider adding new documentation item in `PowerMock`/`PowerMockito` class. 

* Also, look at the [GitHub's Pull Request guide](https://github.com/blog/1943-how-to-write-the-perfect-pull-request)

## General info ##
* Comment on issues or pull requests

* If you know the answer to a question posted to our mailing list - don't hesitate to write a reply. That helps us a lot.

* Also, don't hesitate to ask questions on the mailing list - that helps us improve javadocs/FAQ.

* Please suggest changes to javadoc/exception messages when you find something unclear.

* If you miss a particular feature in PowerMock - browse or ask on the mailing list, show us a sample code and describe the problem.

*  Wondering what to work on? See task/bug labeled with ["for new contributors"](https://github.com/powermock/powermock/issues?q=is%3Aopen+is%3Aissue+label%3A%22for+new+contributors%22). Remember that some feature requests we somewhat not agree with so not everything we want to work on.

*  PowerMock currently uses GitHub for deployment, so you can create a fork of PowerMock. Go to the github project and "Create your own fork". Create a new branch, commit, ..., when you're ready raise a your pull request.

*  Note the project now uses gradle, when your Gradle install is ready, make your IDE project's files (for example gradle idea). Other gradle commands are listed via gradle tasks.

## More on pull requests ##

* On pull requests, please document the change, what it brings, what is the benefit.

* **Clean commit history** in the topic branch in your fork of the repository, even during review. That means that commits are _rebased_ and _squashed_ if necessary, so that each commit clearly changes one things and there are no extraneous fix-ups.

  For that matter it's possible to commit [_semantic_ changes](http://lemike-de.tumblr.com/post/79041908218/semantic-commits). _Tests are an asset, so is history_.

  _Exemple gratia_:
```
Fixes #777 : The new feature
Fixes #777 : Refactors this part of PowerMock to make feature possible
```

* In the code, always test your feature / change, in unit tests and in our acceptance test suite located in [tests](https://github.com/powermock/powermock/tree/master/tests) module. Older tests will be migrated when a test is modified.

* New test methods should follow a snake case convention (`ensure_that_stuff_is_doing_that`), this allows the test name to be fully expressive on intent while still readable.

* Documentation !!! Always document the public API with love. Internals could use some love too but it's arguably not as important. In all cases the code should _auto-document_ itself like any [well designed API](rebased and squashed if necessary, so that each commit clearly changes one things and there are no extraneous fix-ups).

* We use (4) spaces instead of tabs. Make sure line ending is Unix style (LF). More on line ending on the [GitHub help](https://help.github.com/articles/dealing-with-line-endings/).
