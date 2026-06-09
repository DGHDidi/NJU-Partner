import fs from "fs";
const path = "src/views/Home.vue";
let content = fs.readFileSync(path, "utf-8");
const findStr = "import PostCard from '\''@/components/PostCard.vue'\''";
const newStr = findStr + "\nimport { CAMPUS_OPTIONS, POST_TYPE_OPTIONS } from '\''@/constants'\''";
if (content.includes(newStr)) {
  console.log("Already fixed");
} else if (content.includes(findStr)) {
  content = content.replace(findStr, newStr);
  fs.writeFileSync(path, content, "utf-8");
  console.log("Fixed!");
} else {
  console.log("ERROR: Cannot find the import line");
}
