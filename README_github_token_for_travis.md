# What do when github token needs to be updated

The github token used for SlotPuzzle to allow for builds to be deployed
by travis-ci to github needs to be regenetared every 90 days.

The github tokens are stored in github.com/settings/tokens

An email will be sent by github when the github token is about to expire. 
When the github token expires:
  1. click on regenerate token
  2. click on update token
  3. make a note of the token
  4. use travis encrypt <regenerated github token>
  * see below on how to install travis command line interface via gem
  5. Use encrypted token and update the token in .travis.yml
  * under api_key: via secure:

## How to install travis command line interface via gem

### Install Ruby:
  * For Ubuntu:
  ```
  sudo apt-get update
  sudo apt-get install ruby
  ```
### Install travis gem
  * For Ubuntu:
  ```
  sudo gem install travis
  ```
