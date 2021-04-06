# show all scala versions
brew search scala

# install specific version, maybe scala 2.11
brew install scala@2.11

# unlink current scala version
brew unlink scala

# link new scala version
brew link scala@2.11 --force

# check result
scala -version
