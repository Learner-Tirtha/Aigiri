// hash-and-compare.js

const bcrypt = require('bcrypt');

// Replace this with the password you want to test
const password = "Tirtha@21"
// Replace this with the hash you want to compare against
const givenHash = "$2a$10$tb1nAtZAuuo0rYdGDg.3be.r18YFQ1M0wq3pfWRt1bxthRl4yYrQa";

(async () => {
    try {
        // Generate a new hash (just for demo – you can skip this if you already have the hash)
        const newHash = await bcrypt.hash(password, 10);
        console.log("Generated hash of password:", newHash);

        // Compare the password with the given hash
        const isMatch = await bcrypt.compare(password, givenHash);
        console.log("Does the password match the given hash?", isMatch);
    } catch (err) {
        console.error("Error:", err);
    }
})();
