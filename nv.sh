# Examples
# Git tag             -> Sorted Last version -> Output
# v2.3.0              -> 2.3.0               -> 2.3.1
# v2.3.0 v2.3.1       -> 2.3.1               -> 2.3.2
# v2.3.9              -> 2.3.9               -> 2.3.10
# v2.4.0-alpha        -> 2.4.0-alpha         -> 2.4.0
# v2.4.0 v2.4.0-alpha -> 2.4.0               -> 2.4.1


# Example input for testing
#versions="asdf\nv2.4\nv2.3.0\nv2.4.0-alpha\nv2.4.0"
#versions="asdf\nv2.4\nv2.3.0\nv2.4.0-alpha"
#versions="asdf\nv2.4\nv2.3.0\nv2.3.9"

versions=$(git tag)

# find "newest" valid tag "v2.4.0(-alpha)"
# sort args: -r reverse -V natural order (2, 9, 10)
tag=$(echo "$versions" | grep -E "^v[0-9]+\.[0-9]+\.[0-9]+(-.+)?$" | sort -rV | head -n 1)
if [ "$tag" = "" ]; then
 echo "No valid tag found"
 exit 1
fi

# extract version without "-alpha" appendix
version=$(echo "$tag" | sed -E "s/^(v[0-9]+\.[0-9]+\.[0-9]+)(-.+)?$/\1/")

# check if version without appendix exists
v1=$(echo "$versions" | grep -E "^$version$")

if [ "$v1" = "" ]; then
 # Only the version with appendix exists (v2.4.0-alpha), this is then the next version (v2.4.0)
 echo "$version"
 exit 0
fi

# patch version part (v2.4.0 -> "0")
patch=$(echo "$version" | sed -E "s/^v[0-9]+\.[0-9]+\.([0-9]+)$/\1/")
# rest (v2.4.0 -> "v2.4.")
mm=$(echo "$version" | sed -E 's/[0-9]+$//')

# calc next patch version
patch=$((patch+1))

echo "$mm$patch"
