(ns edcmcgen.dictionary)

(def
  ^{:doc "Dictionary between Elite: Dangerous key names and CH Products key codes.
The application will apply replacements in the order they are defined.
Therefore more general replacements should be added at the end."}
  replacements-in-order-of-application
  [["Key_Backspace" "BKSPC"]
   ["Key_Delete" "KBDEL"]
   ["Key_DownArrow" "KBDOWN"]
   ["Key_End" "KBEND"]
   ["Key_Enter" "ENT"]
   ["Key_Home" "KBHOME"]
   ["Key_Insert" "KBINS"]
   ["Key_LeftArrow" "KBLEFT"]
   ["Key_LeftBracket" "["]
   ["Key_LeftShift" "LSHF"]
   ["Key_LeftAlt" "LALT"]
   ["Key_PageDown" "KBPGDN"]
   ["Key_PageUp" "KBPGUP"]
   ["Key_RightArrow" "KBRIGHT"]
   ["Key_RightBracket" "]"]
   ["Key_Space" "SPC"]
   ["Key_Tab" "TAB"]
   ["Key_UpArrow" "KBUP"]
   ["Key_Period" "."]
   ["Key_Comma" ","]
   ["Key_Grave" "`"]
   ["Key_Numpad_Add" "KP+"]
   ["Key_Numpad_Divide" "KP/"]
   ["Key_Numpad_Multiply" "KP*"]
   ["Key_Numpad_" "KP"]
   ["Key_" ""]])
