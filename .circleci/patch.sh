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
[ -r REMOTE_README.md ] && cat REMOTE_README.md > "$SRC/README.md"

e_success "Patch complete"
