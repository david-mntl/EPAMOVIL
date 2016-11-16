Use DBCore;
GO

restore log DBCore 
from disk='Location' 
with stats,recovery

GO