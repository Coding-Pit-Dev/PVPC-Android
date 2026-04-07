#!/usr/bin/env bash
# Captures build/test/lint failures and drafts an Archive entry.
# Called as a PostToolUse hook for the Bash tool.
# Receives event JSON on stdin.

set -euo pipefail

INPUT=$(cat)

TOOL=$(echo "$INPUT" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('tool_name',''))" 2>/dev/null || true)
if [ "$TOOL" != "Bash" ]; then exit 0; fi

OUTPUT=$(echo "$INPUT" | python3 -c "import sys,json; d=json.load(sys.stdin); r=d.get('tool_response',{}); print(r.get('output','') if isinstance(r,dict) else str(r))" 2>/dev/null || true)
COMMAND=$(echo "$INPUT" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('tool_input',{}).get('command',''))" 2>/dev/null || true)

# Detect failure patterns common to Gradle / Kotlin / detekt / ktlint
if echo "$OUTPUT" | grep -qiE "(BUILD FAILED|FAILED|error:|Exception in thread|UnresolvedReference|compilation error|Test failed|AssertionError|detekt found|ktlint)" 2>/dev/null; then
  PENDING="$(dirname "$0")/../archive-pending.json"
  DATE=$(date +%Y-%m-%d)
  python3 - <<PYEOF
import json, os
pending = "$PENDING"
command = """$COMMAND"""
output = """$OUTPUT"""
date = "$DATE"
entry = {
    "date": date,
    "command": command.strip(),
    "output_snippet": output.strip()[:2000]
}
with open(pending, "w") as f:
    json.dump(entry, f, indent=2)
PYEOF
fi
