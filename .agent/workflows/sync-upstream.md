---
description: Sync with upstream sing-box while intelligently preserving custom modifications, with AI handling conflicts.
---

This workflow merges the latest changes from the upstream repository (SagerNet/sing-box-for-android) into your local fork. It is designed to be fully automated by the AI, including conflict resolution that strictly prioritizes your local customizations.

1. **Prerequisites & Setup**
   Ensure you are in the project root.
   // turbo
   `git remote -v`
   (The AI verifies 'upstream' exists. If missing, it will add it: `git remote add upstream https://github.com/SagerNet/sing-box-for-android.git`)

2. **Execute Sync (Fetch, Merge, Push)**
   Fetches upstream history, performs a smart merge favoring local changes (ours) to resolve conflicts automatically, and pushes the result.
   // turbo
   `git fetch upstream; git merge upstream/dev -X ours -m "Merge upstream/dev preserving local customizations"; git push origin dev`
