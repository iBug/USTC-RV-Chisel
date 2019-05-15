#!/bin/bash

: ${SRC:=_site}

e_success() {
  echo -e "\x1B[32;1m[Success]\x1B[0m $*" >&2
}

e_info() {
  echo -e "\x1B[36;1m[Info]\x1B[0m $*" >&2
}

e_warning() {
  echo -e "\x1B[33;1m[Warning]\x1B[0m $*" >&2
}

e_error() {
  echo -e "\x1B[31;1m[Error]\x1B[0m $*" >&2
}

e_info "Patching generated site"

:> "$SRC/.nojekyll"
echo -n "risc-v.ibugone.com" > "$SRC/CNAME"
if [ -r "REMOTE_README.md" ]; then
  cat "REMOTE_README.md" > "$SRC/README.md"
elif [ -r "README.md" ]; then
  cat "README.md" > "$SRC/README.md"
  if [ -n "$CIRCLE_BUILD_NUM" -a -n "$CIRCLE_BUILD_URL" ]; then
    echo >> "$SRC/README.md"
    echo "Deployed from [CircleCI build $CIRCLE_BUILD_NUM]($CIRCLE_BUILD_URL)" >> "$SRC/README.md"
  fi
fi

e_success "Patch complete"
