PR_ID=$1
BRANCH_NAME=$2

git fetch origin pull/$PR_ID/head:$BRANCH_NAME
git checkout $BRANCH_NAME
