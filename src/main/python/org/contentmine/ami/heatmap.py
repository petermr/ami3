import matplotlib
# Run matplotlib headless (allow this script to work on servers as well)
matplotlib.use('Agg')
import pandas as pd
#import seaborn as sns
from numpy.random import randint
import matplotlib.pyplot as plt

fig = plt.figure('')

# Create a dataframe and fill it with random values
#df = pd.DataFrame(randint(0,10,(200,12)),columns=list('abcdefghijkl'))
# /normami/target/totalIntegration/biorxiv/csv
df = pd.read_csv('../../../../../../target/totalIntegration/biorxiv/csv/binomial-gene/cooccurrence.csv', header=None);
df.shape()

#grouped = df.groupby('a')
#rowlength = grouped.ngroups/2 # fix up if odd number of groups
fig, axs = plt.subplots(figsize=(9,4), nrows=2, ncols=rowlength)

targets = zip(grouped.groups.keys(), axs.flatten())
for i, (key, ax) in enumerate(targets):
   sns.heatmap(grouped.get_group(key).corr(), ax=ax,
               xticklabels=(i >= rowlength),
               yticklabels=(i%rowlength==0),
               cbar=False) # Use cbar_ax into single side axis
   ax.set_title('a=%d'%key)

# Save in various formats
fig.savefig('testseabornheatmap.png')
fig.savefig('testseabornheatmap.svg', format='svg')