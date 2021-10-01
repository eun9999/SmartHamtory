from django.db import models

# Create your models here.
# db table make
class Error(models.Model):
    error_register_name = models.CharField(max_length=30)
    error_equip_name = models.CharField(max_length=30)
    error_content = models.TextField()
    def __str__(self):
        return f'{self.error_register_name},{self.error_equip_name},{self.error_content}'
