# Defect Log

Logged in the standard STLC format: reproducible steps, expected vs actual, severity.
Once you point this suite at your own Credit Risk API, seed 2-3 real bugs there
(e.g. an off-by-one in the DTI threshold, or a missing null-check on employment
status) and fill this out for real — that's your strongest interview story.

---

### DEF-001
- **Title:** [example] DTI boundary value incorrectly approved
- **Severity:** High
- **Found by:** NegativeTests.java — boundary value test
- **Steps to reproduce:**
  1. Submit application with DTI ratio exactly at the rejection threshold (e.g. 0.43)
  2. Observe response
- **Expected:** Application flagged as high-risk / rejected (boundary is inclusive)
- **Actual:** Application approved — boundary treated as exclusive
- **Root cause:** `<=` used instead of `<` in risk engine comparison
- **Status:** Fixed & verified by re-run of NegativeTests suite

---

### DEF-002
- **Title:** [example] Missing employment status returns 500 instead of 400
- **Severity:** Medium
- **Found by:** NegativeTests.java — missing field test
- **Steps to reproduce:**
  1. POST to /applications without `employmentStatus` field
  2. Observe response
- **Expected:** 400 Bad Request with a clear validation error
- **Actual:** 500 Internal Server Error (unhandled null pointer)
- **Root cause:** No input validation before risk engine processes the field
- **Status:** Fixed & verified

---

Add real entries here as you find them.
