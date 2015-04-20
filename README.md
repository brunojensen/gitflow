# GitFlow Maven Plugin

The Maven plugin for Vincent Driessen's [successful Git branching model](http://nvie.com/posts/a-successful-git-branching-model/).

This plugin use JGit API provided from [Eclipse](https://eclipse.org/jgit/) and Maven commands.

The picture is a little more complex. Imagine two teams working separated on the same source code, there is a project team and support team.
The project team works only with features and bug fixes. The support team works with hotfixes. 

The project team can work in multiple release at the same time. For example, three release on year.

Besides, there are two testing phases, one is held before delivering to the customer and the other is tested by the customer, until the customer approve the release, the source can not go to master branch.

# Installation

The plugin is available from Maven central.

# Goals Overview

- `gitflow:start-release` 
- * To execute this goal the current branch must be **develop**.
- * Start new **release** branch from **develop** and updates pom(s) with release version. 

- `gitflow:finish-release` 
- * To execute this goal the current branch must be **develop**.
- * Merge **release** branch into **develop**. 
- * Increase pom version based on last Tag created. 
- * Create a new tag.

- `gitflow:start-development` 
- * Start new development branch from **release**.
- * The branch type must be **feature** or **bugfix**.

- `gitflow:finish-development` 
- * Merge branch **development** into **release**.

- `gitflow:start-hotfix` 
- * Start new **hotfix** branch from **master**.
- * Increase the pom version.

- `gitflow:finish-hotfix` 
- * Merge **hotfix** branch into **develop** and **master**.
- * Delete hotfix branch.


**Good luck!**